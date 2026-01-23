import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { PostsComponent } from './pages/posts/posts.component';
import { MeComponent } from './pages/me/me.component';
import { TopicsComponent } from './pages/topics/topics.component';
import { AuthGuard } from './guards/auth.guard';

const routes: Routes = [
  { 
    path: '',
    component: HomeComponent
  },
  { 
    path: 'me',
    canActivate: [AuthGuard], 
    component: MeComponent 
  },
  { 
    path: 'topics',
    canActivate: [AuthGuard], 
    component: TopicsComponent 
  },
  { 
    path: 'posts', 
    canActivate: [AuthGuard], 
    component: PostsComponent 
  },
  { 
    path: '**', 
    redirectTo: '' 
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}