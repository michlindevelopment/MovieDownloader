package com.michlindev.moviedownloader

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.text.Html
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.text.HtmlCompat
import com.michlindev.moviedownloader.data.Movie

object Notification {

    private const val CHANNEL_ID = "channel_id01"
    private const val NOTIFICATION_ID = 1


    fun showNotification(movie: List<Movie>, resource: Bitmap?,context: Context) {

        createNotificationChannel(context)

        lateinit var remoteCollapsedViews: RemoteViews
        lateinit var remoteExpandedViews: RemoteViews

        if (movie.size == 1) {
            remoteCollapsedViews = RemoteViews(MovieDownloader.appContext.packageName, R.layout.notification_normal)
            remoteExpandedViews = RemoteViews(MovieDownloader.appContext.packageName, R.layout.notification_expended)

            remoteCollapsedViews.setTextViewText(R.id.notif_movie_name, movie[0].title)
            remoteCollapsedViews.setImageViewBitmap(R.id.notif_image, resource);

            remoteExpandedViews.setTextViewText(R.id.notif_movie_name_extend, movie[0].title)
            remoteExpandedViews.setImageViewBitmap(R.id.notif_image_extend, resource);
            remoteExpandedViews.setTextViewText(R.id.notif_movie_year_extend, movie[0].year.toString());
            remoteExpandedViews.setTextViewText(R.id.notif_movie_year_sypnosis, Html.fromHtml(movie[0].summary, HtmlCompat.FROM_HTML_MODE_LEGACY));
        } else {
            remoteCollapsedViews = RemoteViews(MovieDownloader.appContext.packageName, R.layout.notification_normal_multiple)
            remoteExpandedViews = RemoteViews(MovieDownloader.appContext.packageName, R.layout.notification_expended_multiple)

            //pop.joinToString(separator = ", ")

            var movies = ""
            movie.forEach {
                movies = movies + ", " + it.title
            }

            //remoteCollapsedViews.setTextViewText(R.id.notif_movie_name_multiple, movies)
            remoteExpandedViews.setTextViewText(R.id.notif_movie_expanded_multiple, movies)

        }


        //start this(MainActivity) on by Tapping notification
        val mainIntent = Intent(context, MainActivity::class.java)
        mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val mainPIntent = PendingIntent.getActivity(
            context, 0,
            mainIntent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        builder.setSmallIcon(R.drawable.ic_notification_icon)
        builder.priority = NotificationCompat.PRIORITY_DEFAULT
        builder.setAutoCancel(true)
        builder.setContentIntent(mainPIntent)
        builder.setStyle(NotificationCompat.DecoratedCustomViewStyle())
        builder.setCustomContentView(remoteCollapsedViews)
        builder.setCustomBigContentView(remoteExpandedViews)
        val notificationManagerCompat = NotificationManagerCompat.from(context)
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build())


    }

    private fun createNotificationChannel(context: Context) {
        val name: CharSequence = "My Notification"
        val description = "My notification description"
        //importance of your notification
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val notificationChannel = NotificationChannel(CHANNEL_ID, name, importance)
        notificationChannel.description = description
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        notificationManager!!.createNotificationChannel(notificationChannel)
    }
}