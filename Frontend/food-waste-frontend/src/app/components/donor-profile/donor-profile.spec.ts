import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';

import { DonorProfileComponent } from './donor-profile';

describe('DonorProfileComponent', () => {
  let component: DonorProfileComponent;
  let fixture: ComponentFixture<DonorProfileComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DonorProfileComponent],
      providers: [provideHttpClient(), provideRouter([])]
    }).compileComponents();

    fixture = TestBed.createComponent(DonorProfileComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
