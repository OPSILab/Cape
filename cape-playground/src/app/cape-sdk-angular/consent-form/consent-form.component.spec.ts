import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ConsentFormComponent } from './consent-form.component';

describe('ConsentFormComponent', () => {
  let component: ConsentFormComponent;
  let fixture: ComponentFixture<ConsentFormComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [ConsentFormComponent],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(ConsentFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
