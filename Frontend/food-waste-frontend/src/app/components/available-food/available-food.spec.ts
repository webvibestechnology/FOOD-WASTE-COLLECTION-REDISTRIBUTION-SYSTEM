import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AvailableFood } from './available-food';

describe('AvailableFood', () => {
  let component: AvailableFood;
  let fixture: ComponentFixture<AvailableFood>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AvailableFood],
    }).compileComponents();

    fixture = TestBed.createComponent(AvailableFood);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
