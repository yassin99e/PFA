import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NewConversationComponent } from './new-conversation.component';

describe('NewConversationComponent', () => {
  let component: NewConversationComponent;
  let fixture: ComponentFixture<NewConversationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [NewConversationComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NewConversationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
