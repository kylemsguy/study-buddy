# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='Conversation',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, verbose_name='ID', serialize=False)),
                ('name', models.CharField(max_length=200)),
                ('created_time', models.DateTimeField(verbose_name='time created')),
            ],
            options={
            },
            bases=(models.Model,),
        ),
        migrations.CreateModel(
            name='Course',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, verbose_name='ID', serialize=False)),
                ('code', models.CharField(max_length=16)),
            ],
            options={
            },
            bases=(models.Model,),
        ),
        migrations.CreateModel(
            name='Message',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, verbose_name='ID', serialize=False)),
                ('time', models.DateTimeField(verbose_name='time sent')),
                ('content', models.TextField()),
            ],
            options={
            },
            bases=(models.Model,),
        ),
        migrations.CreateModel(
            name='User',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, verbose_name='ID', serialize=False)),
                ('email', models.EmailField(max_length=75)),
                ('name', models.CharField(max_length=200)),
                ('reg_date', models.DateTimeField(verbose_name='date registered')),
                ('lat', models.FloatField()),
                ('lon', models.FloatField()),
            ],
            options={
            },
            bases=(models.Model,),
        ),
        migrations.AddField(
            model_name='message',
            name='author',
            field=models.ForeignKey(to='sb_serv.User'),
            preserve_default=True,
        ),
        migrations.AddField(
            model_name='message',
            name='conversation',
            field=models.ForeignKey(to='sb_serv.Conversation'),
            preserve_default=True,
        ),
        migrations.AddField(
            model_name='course',
            name='users',
            field=models.ManyToManyField(to='sb_serv.User'),
            preserve_default=True,
        ),
        migrations.AddField(
            model_name='conversation',
            name='users',
            field=models.ManyToManyField(to='sb_serv.User'),
            preserve_default=True,
        ),
    ]
