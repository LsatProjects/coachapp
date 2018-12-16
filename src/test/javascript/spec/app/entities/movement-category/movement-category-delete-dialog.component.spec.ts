/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { CoachappTestModule } from '../../../test.module';
import { MovementCategoryDeleteDialogComponent } from 'app/entities/movement-category/movement-category-delete-dialog.component';
import { MovementCategoryService } from 'app/entities/movement-category/movement-category.service';

describe('Component Tests', () => {
    describe('MovementCategory Management Delete Component', () => {
        let comp: MovementCategoryDeleteDialogComponent;
        let fixture: ComponentFixture<MovementCategoryDeleteDialogComponent>;
        let service: MovementCategoryService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [CoachappTestModule],
                declarations: [MovementCategoryDeleteDialogComponent]
            })
                .overrideTemplate(MovementCategoryDeleteDialogComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(MovementCategoryDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(MovementCategoryService);
            mockEventManager = fixture.debugElement.injector.get(JhiEventManager);
            mockActiveModal = fixture.debugElement.injector.get(NgbActiveModal);
        });

        describe('confirmDelete', () => {
            it('Should call delete service on confirmDelete', inject(
                [],
                fakeAsync(() => {
                    // GIVEN
                    spyOn(service, 'delete').and.returnValue(of({}));

                    // WHEN
                    comp.confirmDelete(123);
                    tick();

                    // THEN
                    expect(service.delete).toHaveBeenCalledWith(123);
                    expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
                    expect(mockEventManager.broadcastSpy).toHaveBeenCalled();
                })
            ));
        });
    });
});
