import {ErrorHandler, Injectable, Injector} from '@angular/core';
import {MessageService} from 'primeng/api';

import {ErrorLoggingService} from './error-logging.service';

@Injectable()
export class GlobalErrorHandler implements ErrorHandler {
  constructor(private readonly injector: Injector) {
  }

  handleError(error: unknown): void {
    // lazy get services to avoid cyclic DI issues
    const loggingService = this.injector.get(ErrorLoggingService);
    const messageService = this.injector.get(MessageService);

    // developer visibility
    // eslint-disable-next-line no-console
    // console.error('GlobalErrorHandler caught:', error);

    // show a user-friendly message
    try {
      const message = (error as any)?.message || 'An unexpected error occurred';

      // Add a new message to the existing stack
      messageService.add({
        severity: 'error',
        summary: 'Application Error',
        detail: message || 'An unexpected error occurred',
        // sticky: true
      });
    } catch (e) {
      // log the failure to show; don't swallow silently
      // eslint-disable-next-line no-console
      console.error('ErrorService.show failed:', e);
    }

    // attempt to log remotely (no-op if no endpoint configured)
    try {
      loggingService.log(error);
    } catch (e) {
      // eslint-disable-next-line no-console
      console.error('ErrorLoggingService.log failed:', e);
    }
  }
}
