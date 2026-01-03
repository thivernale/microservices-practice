import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LoadingService {
  private _loadingCount = 0;
  private readonly _loadingSubject = new BehaviorSubject<boolean>(false);
  public readonly loading$ = this._loadingSubject.asObservable();

  constructor() {
  }

  get isLoading(): boolean {
    return this._loadingCount > 0;
  }

  show() {
    this._loadingCount++;
    if (this._loadingCount === 1) {
      this._loadingSubject.next(true);
    }
  }

  hide() {
    if (this._loadingCount > 0) {
      this._loadingCount--;
    }
    if (this._loadingCount === 0) {
      this._loadingSubject.next(false);
    }
  }
}
