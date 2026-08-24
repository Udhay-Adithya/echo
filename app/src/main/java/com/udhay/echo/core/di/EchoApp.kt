package com.udhay.echo.core.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.KoinApplication
import org.koin.core.annotation.Module

@Module(includes = [DatabaseModule::class, NetworkModule::class])
@KoinApplication
@ComponentScan("com.udhay.echo")
class EchoApp