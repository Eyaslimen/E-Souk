export interface RegisterRequestDTO {
  username: string;
  email: string;
  password: string;
  phone: string;
  address: string;
  codePostal: string;
  role: string;
  profilePicture?: File;
}

export interface LoginRequestDTO {
  usernameOrEmail: string;
  password: string;
}

export interface AuthResponseDTO {
  token: string;
  message: string;
  username: string;
  email: string;
}