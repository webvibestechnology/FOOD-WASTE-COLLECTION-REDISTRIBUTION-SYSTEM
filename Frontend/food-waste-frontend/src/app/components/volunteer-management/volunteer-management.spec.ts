import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';

import { VolunteerManagementComponent } from './volunteer-management';

describe('VolunteerManagementComponent', () => {
  let component: VolunteerManagementComponent;
  let fixture: ComponentFixture<VolunteerManagementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VolunteerManagementComponent],
      providers: [provideHttpClient(), provideRouter([])]
    }).compileComponents();

    fixture = TestBed.createComponent(VolunteerManagementComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
