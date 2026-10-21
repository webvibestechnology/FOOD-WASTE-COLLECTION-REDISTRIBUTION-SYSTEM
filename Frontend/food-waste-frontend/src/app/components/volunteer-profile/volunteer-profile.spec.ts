import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { VolunteerProfileComponent } from './volunteer-profile';

describe('VolunteerProfileComponent', () => {
  let component: VolunteerProfileComponent;
  let fixture: ComponentFixture<VolunteerProfileComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VolunteerProfileComponent],
      providers: [
        provideHttpClient(),
        provideRouter([])
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(VolunteerProfileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
