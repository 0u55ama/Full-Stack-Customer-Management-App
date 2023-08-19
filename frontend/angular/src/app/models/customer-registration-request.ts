export interface CustomerRegistrationRequest{
  id?: number
  name?: string
  email?: string
  age?: number
  password?: string
  gender?: 'MALE' | 'FEMALE'
}
