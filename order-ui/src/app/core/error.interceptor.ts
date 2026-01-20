import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      let errorMessage = 'An unknown error occurred!';

      if ('ErrorEvent' in globalThis && error.error instanceof ErrorEvent) {
        // Client-side or network error
        errorMessage = `Error: ${error.error.message}`;
      } else {
        // Server-side error
        switch (error.status) {
          case 401:
            errorMessage = 'Unauthorized. Please login again.';
            // Add redirect logic here
            break;
          case 404:
            errorMessage = 'The requested resource was not found.';
            break;
          case 500:
            errorMessage = 'Internal Server Error. Try again later.';
            break;
        }
      }

      // Re-throw the error so it can still be handled by the calling component if needed
      return throwError(() => new Error(errorMessage));
    }),
  );
};
