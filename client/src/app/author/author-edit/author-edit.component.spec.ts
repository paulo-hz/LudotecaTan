import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AuthorEditComponent } from './author-edit.component';

describe('AuthorEdit', () => {
  let component: AuthorEditComponent;
  let fixture: ComponentFixture<AuthorEditComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AuthorEditComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(AuthorEditComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
