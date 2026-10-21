import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';

import { DonorDashboardComponent } from './donor-dashboard';

describe('DonorDashboardComponent', () => {
  let component: DonorDashboardComponent;
  let fixture: ComponentFixture<DonorDashboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DonorDashboardComponent],
      providers: [provideHttpClient(), provideRouter([])]
    }).compileComponents();

    fixture = TestBed.createComponent(DonorDashboardComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
