import { TestBed } from '@angular/core/testing';
import { MessageService } from 'primeng/api';
import { AppComponent } from './app.component';

describe('AppComponent', () => {
  const messageServiceMock = jasmine.createSpyObj('MessageService', ['add', 'clear']);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppComponent],
      providers: [{
        provide: MessageService,
        useValue: messageServiceMock
      }],
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

});
