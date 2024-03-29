import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { MyWellnessComponent } from './mywellness.component';

describe('MywellnessComponent', () => {
  let component: MyWellnessComponent;
  let fixture: ComponentFixture<MyWellnessComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [MyWellnessComponent],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(MyWellnessComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
