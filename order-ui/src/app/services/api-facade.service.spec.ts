import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ApiFacadeService } from './api-facade.service';

describe('ApiFacadeService', () => {
  let service: ApiFacadeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(ApiFacadeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
