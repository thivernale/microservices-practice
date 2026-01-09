import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { SkipLoading } from '../utils/loading/loading.interceptor';

@Injectable({
  providedIn: 'root'
})
export class SearchService {

  private readonly apiUrl = 'https://jsonplaceholder.typicode.com/posts?_page=1&_limit=10';

  constructor(private readonly httpClient: HttpClient) {
  }

  search(query: string) {
    const headers = { 'Content-Type': 'application/json' };
    return this.httpClient.get<any>(`${this.apiUrl}?q=${encodeURIComponent(query)}`, {
      headers,
      context: new HttpContext().set(SkipLoading, true)
    });
  }
}
