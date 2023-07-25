import { Component,signal, OnInit, inject} from '@angular/core';
import { ProductsResponse } from 'src/app/core/interfaces/products.interfaces';
import { DataService } from 'src/app/core/services/data/data.service';

@Component({
  selector: 'app-home-countries',
  templateUrl: './home-countries.component.html',
  styleUrls: ['./home-countries.component.scss']
})
export class HomeCountriesComponent {
   products = signal<ProductsResponse[]>([]);

  dataSerivice = inject(DataService);

  ngOnInit(): void {
    this.getDataProduct();
  }

  getDataProduct() {
    const data = JSON.parse(sessionStorage.getItem('products') || '[]');
    this.dataSerivice.getDataProducts().subscribe((res) => {
      this.products.set(res);
    });
    if (!data) return;
    this.products.set(data);
  }

}