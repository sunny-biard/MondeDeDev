import { User } from './user.interface';
import { Topic } from './topic.interface';

export interface UserProfile extends User {
  subscriptions: Topic[];
}