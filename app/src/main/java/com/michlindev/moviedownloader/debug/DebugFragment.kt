package com.michlindev.moviedownloader.debug

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.michlindev.moviedownloader.MainActivity
import com.michlindev.moviedownloader.Notification
import com.michlindev.moviedownloader.R
import com.michlindev.moviedownloader.data.Movie
import com.michlindev.moviedownloader.databinding.DebugFragmentBinding
import com.michlindev.moviedownloader.main.MovieListRepo
import kotlinx.coroutines.launch


class DebugFragment : Fragment() {

    private val viewModel: DebugViewModel by activityViewModels()

   /* private val CHANNEL_ID = "channel_id01"
    private val NOTIFICATION_ID = 1*/


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val binding = DebugFragmentBinding.inflate(layoutInflater)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.notification.observe(viewLifecycleOwner) {

            lifecycleScope.launch {
                val movies = MovieListRepo.searchMovie("Die Hard")

                val newMovies = mutableListOf<Movie>()
                newMovies.add(movies[1])
                //newMovies.add(movies[2])

                if (newMovies.size == 1) {
                    Glide.with(requireContext())
                        .asBitmap()
                        .load(newMovies[0].large_cover_image)
                        .into(object : CustomTarget<Bitmap>() {
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                Notification.showNotification(newMovies, resource,requireActivity())
                                //imageView.setImageBitmap(resource)
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {
                            }
                        })
                } else {
                    Notification.showNotification(newMovies, null,requireContext())
                }


            }
        }

        return binding.root
    }

   /* private fun showNotification(movie: List<Movie>, resource: Bitmap?) {
        createNotificationChannel()

        lateinit var remoteCollapsedViews: RemoteViews
        lateinit var remoteExpandedViews: RemoteViews

        if (movie.size == 1) {
            remoteCollapsedViews = RemoteViews(requireActivity().packageName, R.layout.notification_normal)
            remoteExpandedViews = RemoteViews(requireActivity().packageName, R.layout.notification_expended)

            remoteCollapsedViews.setTextViewText(R.id.notif_movie_name, movie[0].title)
            remoteCollapsedViews.setImageViewBitmap(R.id.notif_image, resource);

            remoteExpandedViews.setTextViewText(R.id.notif_movie_name_extend, movie[0].title)
            remoteExpandedViews.setImageViewBitmap(R.id.notif_image_extend, resource);
            remoteExpandedViews.setTextViewText(R.id.notif_movie_year_extend, movie[0].year.toString());
            remoteExpandedViews.setTextViewText(R.id.notif_movie_year_sypnosis, Html.fromHtml(movie[0].summary, HtmlCompat.FROM_HTML_MODE_LEGACY));
        } else {
            remoteCollapsedViews = RemoteViews(requireActivity().packageName, R.layout.notification_normal_multiple)
            remoteExpandedViews = RemoteViews(requireActivity().packageName, R.layout.notification_expended_multiple)

            //pop.joinToString(separator = ", ")

            var movies = ""
            movie.forEach {
                movies = movies + ", " + it.title
            }

            //remoteCollapsedViews.setTextViewText(R.id.notif_movie_name_multiple, movies)
            remoteExpandedViews.setTextViewText(R.id.notif_movie_expanded_multiple, movies)

        }


        //start this(MainActivity) on by Tapping notification
        val mainIntent = Intent(requireContext(), MainActivity::class.java)
        mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val mainPIntent = PendingIntent.getActivity(
            requireContext(), 0,
            mainIntent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
        builder.setSmallIcon(R.drawable.ic_notification_icon)
        builder.priority = NotificationCompat.PRIORITY_DEFAULT
        builder.setAutoCancel(true)
        builder.setContentIntent(mainPIntent)
        builder.setStyle(NotificationCompat.DecoratedCustomViewStyle())
        builder.setCustomContentView(remoteCollapsedViews)
        builder.setCustomBigContentView(remoteExpandedViews)
        val notificationManagerCompat = NotificationManagerCompat.from(requireContext())
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build())


    }

    private fun createNotificationChannel() {
        val name: CharSequence = "My Notification"
        val description = "My notification description"
        //importance of your notification
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val notificationChannel = NotificationChannel(CHANNEL_ID, name, importance)
        notificationChannel.description = description
        val notificationManager = requireActivity().getSystemService(NOTIFICATION_SERVICE) as NotificationManager?
        notificationManager!!.createNotificationChannel(notificationChannel)
    }*/

}