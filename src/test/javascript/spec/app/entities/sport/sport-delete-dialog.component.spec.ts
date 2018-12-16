/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { CoachappTestModule } from '../../../test.module';
import { SportDeleteDialogComponent } from 'app/entities/sport/sport-delete-dialog.component';
import { SportService } from 'app/entities/sport/sport.service';

describe('Component Tests', () => {
    describe('Sport Management Delete Component', () => {
        let comp: SportDeleteDialogComponent;
        let fixture: ComponentFixture<SportDeleteDialogComponent>;
        let service: SportService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [CoachappTestModule],
                declarations: [SportDeleteDialogComponent]
            })
                .overrideTemplate(SportDeleteDialogComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(SportDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(SportService);
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
