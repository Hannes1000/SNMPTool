-- Changes to rfc1573b - IANAifType-MIB
--     Extracted from RFC1573 - the combined IANAifType-MIB & IF-MIB
--     Added the following import: mib-2 from RFC1213-MIB.
--     Removed the following unneeded import: OBJECT-TYPE from
--         SNMPv2-SMI.
-- dperkins@scruznet.com
  "This data type is used as the syntax of the ifType
               object in the (updated) definition of MIB-II's
               ifTable.

               The definition of this textual convention with the
               addition of newly assigned values is published
               periodically by the IANA, in either the Assigned
               Numbers RFC, or some derivative of it specific to
               Internet Network Management number assignments.  (The
               latest arrangements can be obtained by contacting the
               IANA.)

               Requests for new values should be made to IANA via
               email (iana@isi.edu).

               The relationship between the assignment of ifType
               values and of OIDs to particular media-specific MIBs
               is solely the purview of IANA and is subject to change
               without notice.  Quite often, a media-specific MIB's
               OID-subtree assignment within MIB-II's 'transmission'
               subtree will be the same as its ifType value.
               However, in some circumstances this will not be the
               case, and implementors must not pre-assume any
               specific relationship between ifType values and
               transmission subtree OIDs."                                         �"The MIB module which defines the IANAifType textual
               convention, and thus the enumerated values of the
               ifType object defined in MIB-II's ifTable."	"        Internet Assigned Numbers Authority

                   Postal: USC/Information Sciences Institute
                           4676 Admiralty Way, Marina del Rey, CA 90292

                   Tel:    +1  310 822 1511
                   E-Mail: iana@isi.edu"                  