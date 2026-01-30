import { Topic } from "./topic.interface";
import { User } from "./user.interface";

export interface Post {
  id: number;
  title: string;
  content: string;
  createdAt: Date;
  updatedAt: Date;
  topic: Topic;
  user: User;
}