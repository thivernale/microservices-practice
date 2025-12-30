import {Component, Input} from '@angular/core';
import {Card} from 'primeng/card';
import {Button} from 'primeng/button';
import {Message} from 'primeng/message';

@Component({
  selector: 'app-product-card',
  imports: [
    Card,
    Button,
    Message
  ],
  templateUrl: './product-card.component.html',
  styles: ``
})
export class ProductCardComponent {
  @Input() product!: any;

}
