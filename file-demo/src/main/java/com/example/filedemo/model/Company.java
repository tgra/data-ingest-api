package com.example.filedemo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "company")
public class Company {
	
        
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name="idx")
		public String id;
        	
        @Column(name="unique_id")
            public String UniqueIdentifier;
        
        @Column(name="company_number")
        public String CompanyNumber;
        
        @Column(name="company_name")
        public String CompanyName;
        
        @Column(name="event_type")
        public String EventType;
        
        @Column(name="event_date")
        public String EventDate;
        
}