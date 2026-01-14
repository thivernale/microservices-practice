import { Component, effect, input, Input, model, OnInit, output, signal } from '@angular/core';
import { Button } from 'primeng/button';
import { Card } from 'primeng/card';
import { Message } from 'primeng/message';
import { ProductResponse } from '../../services/product/models/product-response';
import { LoadingService } from '../../utils/loading/loading.service';

@Component({
  selector: 'app-product-card',
  imports: [
    Card,
    Button,
    Message
  ],
  templateUrl: './product-card.component.html',
})
export class ProductCardComponent implements OnInit {
  @Input() product!: any;
// signal input allows value to be bound from parent component
  productResponse = input.required<ProductResponse>();
// model input allows to keep data in sync with 2-way binding
  isChecked = model(false);

  tick = signal(0);

  selected = output<ProductResponse['id']>();

  selectedEff = effect(
    () => {
      return this.selected.emit(String(this.tick()));
    }
  )

  constructor(private readonly loadingService: LoadingService) {
  }

  toggleChecked() {
    this.isChecked.update(() => !this.isChecked());
  }

  ngOnInit() {
    /*setInterval(
      () => this.tick.update(t => t + 1),
      1000
    )*/
    this.tick.set(0);
  }

  protected async saveProduct() {
    try {
      this.loadingService.show();
      // Simulate a save operation
      await this.delay(1000);

      // Emit the selected product ID
      //this.selected.emit(this.productResponse().id);
      // increment tick
      this.tick.update(t => t + 1);
    } catch (error) {
      console.error('Error saving product:', error);
    } finally {
      this.loadingService.hide();
    }
  }

  private delay(ms: number) {
    return new Promise((resolve) => setTimeout(resolve, ms));
  }
}
