import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';

import { DonationManagementComponent } from './donation-management';

describe('DonationManagementComponent', () => {
  let component: DonationManagementComponent;
  let fixture: ComponentFixture<DonationManagementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DonationManagementComponent],
      providers: [provideHttpClient(), provideRouter([])]
    }).compileComponents();

    fixture = TestBed.createComponent(DonationManagementComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
