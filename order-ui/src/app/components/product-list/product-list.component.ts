import { Component, effect, Input, viewChildren } from '@angular/core';
import { ProductResponse } from '../../services/product/models/product-response';
import { ProductCardComponent } from '../product-card/product-card.component';

@Component({
  selector: 'app-product-list',
  imports: [
    ProductCardComponent
  ],
  templateUrl: './product-list.component.html',
})
export class ProductListComponent {
  @Input() products!: Array<ProductResponse> | undefined | null;

  // signal query API - exposes query results as a signal
  children = viewChildren(ProductCardComponent);


  constructor() {
    effect(() => {
      console.log('Number of ProductCardComponent children:', this.children().length);
    })
  }

  protected onProductSelected($event: ProductResponse["id"]) {
    console.log('Product selected with ID:', $event);
  }
}
