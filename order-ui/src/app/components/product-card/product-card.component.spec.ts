import { signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { ProductResponse } from '../../services/product/models/product-response';

import { ProductCardComponent } from './product-card.component';

describe('ProductCardComponent', () => {
  let component: ProductCardComponent;
  let fixture: ComponentFixture<ProductCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProductCardComponent],
      providers: [provideNoopAnimations()]
    })
      .compileComponents();

    fixture = TestBed.createComponent(ProductCardComponent);
    const product = {} as ProductResponse;
    fixture.componentRef.setInput('product', product);
    fixture.componentRef.setInput('productResponse', signal(product));
    component = fixture.componentInstance;
    fixture.detectChanges();
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
