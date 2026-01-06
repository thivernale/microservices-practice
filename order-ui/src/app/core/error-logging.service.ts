import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ErrorLoggingService {
  private readonly loggingUrl = (environment as any).errorLoggingUrl || null;

  constructor(private readonly http: HttpClient) {
  }

  log(error: unknown) {
    // don't block; if no logging endpoint configured, skip
    if (!this.loggingUrl) {
      return;
    }

    const payload = {
      message: (error as any)?.message || String(error),
      stack: (error as any)?.stack || null,
      timestamp: new Date().toISOString()
    };

    this.http.post(this.loggingUrl, payload)
      .pipe(catchError(() => of(null)))
      .subscribe();
  }
}
