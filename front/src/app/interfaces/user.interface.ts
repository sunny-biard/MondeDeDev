import { Topic } from "./topic.interface";

export interface User {
  id: number;
  username: string;
  email: string;
  createdAt: Date;
  updatedAt: Date;
  subscriptions: Topic[];
}