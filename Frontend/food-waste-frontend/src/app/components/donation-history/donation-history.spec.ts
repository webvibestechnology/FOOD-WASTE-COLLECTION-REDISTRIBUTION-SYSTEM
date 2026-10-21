
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';

import { DonationHistoryComponent } from './donation-history';

describe('DonationHistoryComponent', () => {
  let component: DonationHistoryComponent;
  let fixture: ComponentFixture<DonationHistoryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DonationHistoryComponent],
<<<<<<< HEAD
=======
      providers: [provideHttpClient(), provideRouter([])]
>>>>>>> 64475a0 (complete)
    }).compileComponents();

    fixture = TestBed.createComponent(DonationHistoryComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
