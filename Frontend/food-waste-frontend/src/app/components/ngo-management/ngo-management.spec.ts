import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';

import { NgoManagementComponent } from './ngo-management';

describe('NgoManagementComponent', () => {
  let component: NgoManagementComponent;
  let fixture: ComponentFixture<NgoManagementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NgoManagementComponent],
      providers: [provideHttpClient(), provideRouter([])]
    }).compileComponents();

    fixture = TestBed.createComponent(NgoManagementComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
