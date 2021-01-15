import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CapeSdkAngularComponent } from './cape-sdk-angular.component';

describe('CapeSdkAngularComponent', () => {
  let component: CapeSdkAngularComponent;
  let fixture: ComponentFixture<CapeSdkAngularComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CapeSdkAngularComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CapeSdkAngularComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
