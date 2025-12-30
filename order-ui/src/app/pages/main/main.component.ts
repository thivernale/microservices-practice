import {Component, OnInit} from '@angular/core';
import {SearchService} from '../../services/search.service';
import {Toolbar} from 'primeng/toolbar';
import {ButtonDirective} from 'primeng/button';
import {ProductListComponent} from '../../components/product-list/product-list.component';

@Component({
  selector: 'app-main',
  imports: [
    Toolbar,
    ButtonDirective,
    ProductListComponent
  ],
  templateUrl: './main.component.html',
  styles: ``
})
export class MainComponent implements OnInit {
  protected products: Array<Record<string, any>> | undefined | null;

  constructor(private readonly searchService: SearchService) {
  }

  ngOnInit(): void {
    this.search()
  }

  search() {
    this.searchService.search('example query').subscribe({
      next: response => {
        console.log(response);
      },
      error: err => {
        this.products = [];
        this.products.push(
          {
            id: 1,
            name: 'Sample Product 1',
            description: 'This is a sample product used as fallback data.',
            price: 19.99,
            imageUrl: 'https://via.placeholder.com/150'
          },
          {
            id: 2,
            name: 'Sample Product 2',
            description: 'This is another sample product used as fallback data.',
            price: 29.99,
            imageUrl: 'https://via.placeholder.com/150'
          }
        )
        console.error('Search error:', err);
      }
    });
  }

  protected openNewOrderDialog() {
    console.info("Open New Order Dialog");
  }
}
