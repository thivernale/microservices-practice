import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ProductControllerService} from './product/services';
import {ConfigService} from './config.service';

@Injectable({
  providedIn: 'root'
})
export class SearchService {

  private readonly apiUrl = 'https://api.example.com/search';

  constructor(private readonly httpClient: HttpClient) {
  }

  search(query: string) {
    const headers = {'Content-Type': 'application/json'};
    return this.httpClient.get<any>(`${this.apiUrl}?q=${encodeURIComponent(query)}`, {headers});
  }
}
