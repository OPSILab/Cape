import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { WeightComponent } from './weight.component';

describe('WeightComponent', () => {
  let component: WeightComponent;
  let fixture: ComponentFixture<WeightComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [WeightComponent],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(WeightComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
