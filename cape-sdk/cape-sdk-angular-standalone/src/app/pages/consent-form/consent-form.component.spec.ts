import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ConsentFormComponent2 } from './consent-form.component';

describe('ConsentFormComponent', () => {
  let component: ConsentFormComponent2;
  let fixture: ComponentFixture<ConsentFormComponent2>;

  beforeEach(
    waitForAsync(() => {
      void TestBed.configureTestingModule({
        declarations: [ConsentFormComponent2],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(ConsentFormComponent2);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
