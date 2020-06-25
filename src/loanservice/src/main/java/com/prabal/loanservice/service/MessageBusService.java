/**
 * 
 */
package com.prabal.loanservice.service;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Abstract base class with primitive implementation of a Pub-Sub messaging queue, based on RxJava Subjects.
 * In real-time scenario this must be replaced by a reliable messaging platform
 * 
 * @author Prabal Nandi
 *
 */
public abstract class MessageBusService<T> {

	private final PublishSubject<T> subject = PublishSubject.create();

	public void send(T message) {
		this.subject.onNext(message);
	}

	public Observable<T> toObservable() {
		return this.subject;
	}
}