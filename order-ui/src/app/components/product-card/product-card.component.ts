import { Component, input, Input, model, OnInit, signal } from '@angular/core';
import { Button } from 'primeng/button';
import { Card } from 'primeng/card';
import { Message } from 'primeng/message';
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
  showMessage = input.required<boolean>();
// model input allows to keep data in sync with 2-way binding
  isChecked = model(false);

  tick = signal(0);

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
