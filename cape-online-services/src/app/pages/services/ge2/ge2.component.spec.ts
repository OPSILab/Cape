import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { Ge2Component } from './ge2.component';

describe('Ge2Component', () => {
  let component: Ge2Component;
  let fixture: ComponentFixture<Ge2Component>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ Ge2Component ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(Ge2Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
