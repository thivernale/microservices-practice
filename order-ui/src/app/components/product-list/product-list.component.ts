import {Component, Input} from '@angular/core';
import {ProductCardComponent} from '../product-card/product-card.component';

@Component({
  selector: 'app-product-list',
  imports: [
    ProductCardComponent
  ],
  templateUrl: './product-list.component.html',
})
export class ProductListComponent {
  @Input() products!: Array<Record<string, any>> | undefined | null;

}
