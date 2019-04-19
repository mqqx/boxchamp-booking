package com.hammer.apps.boxchampbooking.component;

import com.hammer.apps.boxchampbooking.model.Booking;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends PagingAndSortingRepository<Booking, Long> {
	Iterable<Booking> findAllByUserUsername(String username);
}
