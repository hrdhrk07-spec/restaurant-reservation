import {Page} from '@playwright/test';

export class ReservationInputPage {
    constructor(private page: Page) {
    }

    async fillReservationForm(reservedAt: string, numberOfGuests: string) {
        await this.page.evaluate((date) => {
            const input = document.querySelector('#reservedAt') as any;
            input._flatpickr.setDate(date);
        }, reservedAt);
        await this.page.locator('#numberOfGuests').fill(numberOfGuests);
    }

    async clickSeatAvailabilityButton() {
        await this.page.getByRole('button', { name: '空席確認' }).click();
    }

    async clickSeatDetailRadioButton(personPerSeat: string) {
        await this.page.getByRole('radio', { name: personPerSeat }).click();
    }

    async clickSeatSelectionButton() {
        await this.page.getByRole('button', { name: '席決定' }).click();
    }

}