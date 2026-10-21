import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';

import { NgoDashboardComponent } from './ngo-dashboard';

describe('NgoDashboardComponent', () => {
  let component: NgoDashboardComponent;
  let fixture: ComponentFixture<NgoDashboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NgoDashboardComponent],
      providers: [provideHttpClient(), provideRouter([])]
    }).compileComponents();

    fixture = TestBed.createComponent(NgoDashboardComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
