import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { MyMeasurementsComponent } from './mymeasurements.component';

describe('MymeasurementsComponent', () => {
  let component: MyMeasurementsComponent;
  let fixture: ComponentFixture<MyMeasurementsComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [MyMeasurementsComponent],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(MyMeasurementsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
