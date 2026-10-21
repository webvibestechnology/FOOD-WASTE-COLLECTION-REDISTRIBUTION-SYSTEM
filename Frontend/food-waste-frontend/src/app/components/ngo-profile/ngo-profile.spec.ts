import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { NgoProfileComponent } from './ngo-profile';

describe('NgoProfileComponent', () => {
  let component: NgoProfileComponent;
  let fixture: ComponentFixture<NgoProfileComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NgoProfileComponent],
      providers: [
        provideHttpClient(),
        provideRouter([])
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(NgoProfileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
