import {
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
  inject,
} from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { CookieService } from 'ngx-cookie-service';
import { Notify } from 'notiflix';
import { take } from 'rxjs';
import {
  LoginResponse,
  UserData,
} from 'src/app/core/interfaces/auth.interfaces';
import { AuthService } from 'src/app/core/services/auth/auth.service';
import { UserService } from 'src/app/core/services/user/user.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent implements OnInit {
  @Output() visible = new EventEmitter<boolean>();
  @Input() showLogo = true;
  @Input() isNormalLogin = true;
  loginForm!: FormGroup;

  authService = inject(AuthService);
  formBuilder = inject(FormBuilder);
  cookieService = inject(CookieService);
  router = inject(Router);
  userService = inject(UserService);

  ngOnInit(): void {
    this.loginForm = this.initFormLogin();

    Notify.init({ position: 'right-bottom' });
  }

  initFormLogin(): FormGroup {
    return this.formBuilder.group({
      email: [
        '',
        [
          Validators.required,
          Validators.pattern(/^[\w-]+(\.[\w-]+)*@([\w-]+\.)+[a-zA-Z]{2,7}$/),
        ],
      ],
      password: ['', [Validators.required, Validators.minLength(6)]],
    });
  }

  auth() {
    // const { email, password } = this.loginForm.controls;
    // console.log(password?.errors?.['minlength']);
    if (this.loginForm.invalid) return;
    this.login();
  }

  login() {
    this.authService
      .login(this.loginForm.value)
      .pipe(take(1))
      .subscribe({
        next: (resp: LoginResponse) => {
          // this.cookieService.set('accessToken', resp.token);
          this.getUserData(resp.token);

          if (this.router.routerState.snapshot.url === '/create-user') {
            this.router.navigate(['/']);
          }
        },
        error: () => {
          this.showMessageFailed();
          console.clear();
        },
      });
  }

  getUserData(accessToken: string) {
    this.authService
      .renewSession(accessToken)
      .pipe(take(1))
      .subscribe((resp: UserData) => {
        this.cookieService.set('accessToken', resp.token);
        const newUserData = { ...resp };
        delete newUserData.password;

        localStorage.setItem('userData', JSON.stringify(newUserData));
        this.authService.setAutenticate(true);

        const token = this.cookieService.get('accessToken');

        this.userService.refreshFavorites(token);
      });
  }

  closeDialog() {
    this.visible.emit(false);
  }

  showMessageFailed(): void {
    Notify.failure('Correo o contraseña incorrectos');
  }
}
