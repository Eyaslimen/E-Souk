interface ProductDetailsDto {
  productId: string;
  name: string;
  description: string;
  price: number;
  picture: string;
  shopName: string;
  availableAttributes: { [key: string]: string[] };
}
interface SelectedAttributes {
  [attributeName: string]: string;
}