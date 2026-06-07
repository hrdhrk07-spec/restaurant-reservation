export const TEST_USER_EMAIL = getEnv('TEST_USER_EMAIL');
export const TEST_USER_PASSWORD = getEnv('TEST_USER_PASSWORD');
export const TEST_ADMIN_EMAIL = getEnv('TEST_ADMIN_EMAIL');
export const TEST_ADMIN_PASSWORD = getEnv('TEST_ADMIN_PASSWORD');
export const TEST_CREATE_USER_PASSWORD = getEnv('TEST_CREATE_USER_PASSWORD');
export const TEST_LOCATION = getEnv('TEST_LOCATION');
export const TEST_CUISINE_TYPE = getEnv('TEST_CUISINE_TYPE');
export const TEST_RESTAURANT_NAME = getEnv('TEST_RESTAURANT_NAME');
export const TEST_HOLIDAY = getEnv('TEST_HOLIDAY');
export const TEST_HOLIDAY_NUMBER1 = getEnv('TEST_HOLIDAY_NUMBER1');
export const TEST_HOLIDAY_NUMBER2 = getEnv('TEST_HOLIDAY_NUMBER2');
export const TEST_RECEPTION_TIME = getEnv('TEST_RECEPTION_TIME');
export const TEST_NUMBER_OF_GUESTS = getEnv('TEST_NUMBER_OF_GUESTS');
export const TEST_SEAT_DETAIL1 = getEnv('TEST_SEAT_DETAIL1');
export const TEST_SEAT_DETAIL2 = getEnv('TEST_SEAT_DETAIL2');
export const TEST_RESERVATION_STATUS_CONFIRMED = getEnv('TEST_RESERVATION_STATUS_CONFIRMED');
export const TEST_RESERVATION_STATUS_CANCELLED = getEnv('TEST_RESERVATION_STATUS_CANCELLED');

function getEnv(key: string): string {
    const value = process.env[key];
    if(value == undefined){
        throw new Error(`環境変数：${key}が未設定です。`);
    }
    return value;
}