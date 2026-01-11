import { inject, Injectable } from '@angular/core';
import { CustomerControllerService } from './customer/services';
import { OrderControllerService } from './order/services';
import { ProductControllerService } from './product/services';

@Injectable({
  providedIn: 'root'
})
export class ApiFacadeService {
  public readonly customer = inject(CustomerControllerService);
  public readonly order = inject(OrderControllerService);
  public readonly product = inject(ProductControllerService);
}
