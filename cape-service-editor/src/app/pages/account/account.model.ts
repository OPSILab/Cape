export interface Account {
    account_info: AccountInfo,
    language: string,
    notification: string[]
}
export interface AccountInfo {
    firstname: string,
    lastname: string,
    email: string,
    phone: number
}
